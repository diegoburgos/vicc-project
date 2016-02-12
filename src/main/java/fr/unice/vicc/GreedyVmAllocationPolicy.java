package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GreedyVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public GreedyVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new LinkedHashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new LinkedHashMap<>();
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
        return null;
    }

    private boolean canAllocateConsideringRAMAndMIPS (Host host, Vm vm) {
        return host.getRamProvisioner().getAvailableRam() >= vm.getRam() &&
                host.getAvailableMips() >= vm.getMips();
    }

    private boolean hasAviableCore (Host host, Vm vm) {
        for (Pe pe : host.getPeList()) {
            if (pe.getPeProvisioner().getAvailableMips()>=vm.getMips()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Iterator<Host> it = super.getHostList().iterator();
        Host bestHost = null;
        double bestHostValue = Double.MAX_VALUE;
        Host currentItHost;
        double currentValue;
        if (it.hasNext()) {
            do {
                currentItHost = it.next();
                currentValue = currentItHost.getAvailableMips();
                if (currentValue <= bestHostValue && canAllocateConsideringRAMAndMIPS(currentItHost, vm)
                        && hasAviableCore(currentItHost, vm)) {
                    bestHostValue = currentValue;
                    bestHost = currentItHost;
                }
            } while (it.hasNext());
        }
        return allocateHostForVm(vm, bestHost);
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        }
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        if (hoster.containsKey(vm)) {
            hoster.remove(vm).vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        if (hoster.containsKey(vm)) {
            return hoster.get(vm);
        }
        return null;
    }

    @Override
    public Host getHost(int vmId, int userId) {
        /*
        This should be changed, a hash map should not be iterated
         */
        for (Map.Entry<Vm, Host> entry : hoster.entrySet()) {
            if (entry.getKey().getId()==vmId && entry.getKey().getUserId()==userId) {
                return entry.getValue();
            }
        }
        return null;
    }
}
