package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fhermeni2 on 16/11/2015.
 */
public class NextFitVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public NextFitVmAllocationPolicy(List<? extends Host> list) {
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

    private static int listCount = 0;
    @Override
    public synchronized boolean allocateHostForVm(Vm vm) {
        List<Host> hostList = super.getHostList();
        int currentCount = listCount;
        boolean assigned = false;
        do {
            if (currentCount==hostList.size()) {
                currentCount = 0;
            }
            if (!(assigned = this.allocateHostForVm(vm, hostList.get(currentCount)))) {
                currentCount++;
            }
        } while (!assigned && listCount != currentCount);
        listCount = currentCount;
        return assigned;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            //System.out.println("Allocating vm " + vm.getId() + " in host " + host.getId());
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
