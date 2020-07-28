package test.stone.communication.netty;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
//
//import com.rtzt.protocol.ProtocolBuilder;
//import com.rtzt.protocol.ProtocolBuilderImpl;


@Component
public class MyHeart {
	@Autowired
	private ObuServer obuServer;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//    @Scheduled(fixedRate = 10000)
//    public void timerRate() {
//        System.out.println(dateFormat.format(new Date()));
//    }
    
    //第一次延迟1秒执行，当执行完后2秒再执行
//    @Scheduled(initialDelay = 5000, fixedDelay = 2000)
    public void timerInit() {
//        System.out.println("init : "+dateFormat.format(new Date()));
        heartTest();
    }

    
    
    //每天20点16分50秒时执行
//    @Scheduled(cron = "50 16 20 * * ?")
//    public void timerCron() {
//        System.out.println("current time : "+ dateFormat.format(new Date()));
//    }

	public void heartTest() {
		//ProtocolBuilder p = new ProtocolBuilderImpl();

		Runnable r = new Runnable() {
			@Override
			public void run() {
	 
				if(obuServer==null)
				{
					System.out.println("null-------------");
					return;
				}
				
				obuServer.publish("heartBeat".getBytes());
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) { 
//					e.printStackTrace();
//				}
			}

		 
		};

		new Thread(r).start();
	}
	
	
	
}
