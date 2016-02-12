package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Worst fit: The memory manager places a process in the largest block of unallocated memory available.
 */
public class WorstFitVmAllocationPolicy extends VmAllocationPolicy {
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public WorstFitVmAllocationPolicy(List<? extends Host> list) {
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

    /**
     * This method assign to the host with more ram and mips available the vm
     * max(mips*ram)
     * @param vm
     * @return
     */
    @Override
    public boolean allocateHostForVm(Vm vm) {
        Iterator<Host> it = super.getHostList().iterator();
        Host bestHost = null;
        double bestHostValue = 0;
        Host currentItHost = null;
        double currentValue;
        if (it.hasNext()) {
            do {
                currentItHost = it.next();
                currentValue = currentItHost.getRamProvisioner().getAvailableRam()*currentItHost.getAvailableMips();
                if (currentValue > bestHostValue) {
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
