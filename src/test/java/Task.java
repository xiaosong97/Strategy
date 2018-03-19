import dataProc.Strategy;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimerTask;
/**
 * 执行内容
 * @author admin_Hzw
 *
 */
public class Task extends TimerTask{
    private int i = 0;
    Strategy strategy;
    public Task(Strategy strategy) {
        this.strategy = strategy;
    }

    public void run() {
        strategy.publishTradeInfo(i++);
    }
}
