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

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Iterator<Host> it = super.getHostList().iterator();
        Host hostWithMoreRAMAviable = null;
        int hostWithMoreRAMAviableValue = 0;
        Host hostWithMoreMIPSAviable = null;
        double hostWithMoreMIPSviableValue = 0;
        boolean noAssigned = false;
        Host currentItHost = null;
        if (it.hasNext()) {
            do {
                currentItHost = it.next();
                if (hostWithMoreRAMAviable == null ||
                        currentItHost.getRamProvisioner().getAvailableRam()>hostWithMoreRAMAviableValue) {
                    hostWithMoreRAMAviable = currentItHost;
                    hostWithMoreRAMAviableValue = currentItHost.getRamProvisioner().getAvailableRam();
                }
                if (hostWithMoreMIPSAviable == null ||
                        currentItHost.getAvailableMips()>hostWithMoreMIPSviableValue) {
                    hostWithMoreMIPSAviable = currentItHost;
                    hostWithMoreMIPSviableValue = currentItHost.getAvailableMips();
                }
            } while (it.hasNext());
        }
        noAssigned = allocateHostForVm(vm, hostWithMoreRAMAviable);
        return noAssigned;
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
