package test.stone.communication.message;

public abstract class TestMessage extends SimpleMessage implements MessageCode {


    public TestMessage(byte msgType, short len) {
        setHeader(new Header(msgType,len));
    }

    //volatile
    @Override
    public Header getHeader() {
        return super.getHeader();
    }

    public TestMessage(byte msgType){
        setHeader(new Header(msgType));
    }
}
