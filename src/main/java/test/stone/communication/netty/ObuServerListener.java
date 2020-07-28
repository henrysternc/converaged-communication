package test.stone.communication.netty;

//import java.util.List;

public interface ObuServerListener {    
    void onChipMessage(byte bytes[]);
    void onSimpleMessage(String msg);
}
