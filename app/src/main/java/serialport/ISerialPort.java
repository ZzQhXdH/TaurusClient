package serialport;

/**
 * Created by xdhwwdz20112163.com on 2018/1/13.
 * 串口接口,可以是UsbToUsart也可以是真实的SerialPort
 */

public interface ISerialPort {

    boolean write(byte[] bytes);

    int read(byte[] bytes);

    boolean isOpen();

    boolean close();

    boolean open(int baud_rate); // 波特率可以调整,数据位固定为8位,无校验,无流控,1位停止位
}
