import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

// 定义责任链的抽象类
abstract class Handler {
    protected Handler nextHandler;

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handleRequest(Patient patient);
}

// 定义病患类
class Patient {
    public String name;
    public String condition;  // 病情（简单、复杂、心血管）

    public Patient(String name, String condition) {
        this.name = name;
        this.condition = condition;
    }
}

// 基层医院处理类
class PrimaryHospitalHandler extends Handler {
    @Override
    public void handleRequest(Patient patient) {
        if (patient.condition.equals("简单")) {
            System.out.println("基层医院处理病患：" + patient.name);
        } else {
            System.out.println("基层医院无法处理，转交调度中心");
            if (nextHandler != null) {
                nextHandler.handleRequest(patient);
            }
        }
    }
}

// PCI医院处理类
class PCIHospitalHandler extends Handler {
    @Override
    public void handleRequest(Patient patient) {
        if (patient.condition.equals("心血管")) {
            System.out.println("PCI医院处理病患：" + patient.name);
        } else {
            System.out.println("PCI医院无法处理，转交给下一环节");
            if (nextHandler != null) {
                nextHandler.handleRequest(patient);
            }
        }
    }
}

// 调度中心处理类
class DispatchCenterHandler extends Handler {
    @Override
    public void handleRequest(Patient patient) {
        System.out.println("调度中心分配救护车，转送病患：" + patient.name + " 到PCI医院");
        if (nextHandler != null) {
            nextHandler.handleRequest(patient);
        }
    }
}


// 救护车处理类
class AmbulanceHandler extends Handler {
    @Override
    public void handleRequest(Patient patient) {
        System.out.println("救护车转运病患：" + patient.name);
    }
}


// 测试责任链
public class HospitalSystem {
    public static void main(String[] args) throws IOException {
        // 创建责任链
        Handler primaryHospital = new PrimaryHospitalHandler();
        Handler dispatchCenter = new DispatchCenterHandler();
        Handler ambulance = new AmbulanceHandler();
        Handler pciHospital = new PCIHospitalHandler();

        // 设置责任链顺序
        primaryHospital.setNextHandler(dispatchCenter);
        dispatchCenter.setNextHandler(ambulance);
        ambulance.setNextHandler(pciHospital);

        // 模拟病人请求
         for (int i = 0; i < 10000000; i++) {
             if(i % 50 == 0){
                     printMemoryUsage(i);
             }
             Patient patient = new Patient("张三" + i, "心血管");
             primaryHospital.handleRequest(patient);
         }



    }
    public static void printMemoryUsage(int iteration) throws IOException {
        // 获取内存使用情况
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();      // JVM总内存
        long freeMemory = runtime.freeMemory();        // JVM空闲内存
        long usedMemory = totalMemory - freeMemory;    // JVM使用的内存
        //写入自己电脑文件
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("src/redblacktree/11.txt",true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bufferedWriter.write("Iteration: " + iteration);
        bufferedWriter.write("Total Memory: " + totalMemory / 1024 / 1024 + " MB ");
        bufferedWriter.write("Free Memory: " + freeMemory / 1024 / 1024 + " MB ");
        bufferedWriter.write("Used Memory: " + usedMemory / 1024 / 1024 + " MB ");
        bufferedWriter.write(" CPU Load: " + osBean.getSystemLoadAverage());
        bufferedWriter.newLine();
        bufferedWriter.close();


    }
}
