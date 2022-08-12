# netty 

## 功能点
1. 基于 LengthFieldBasedFrameDecoder 进行粘包解码处理
2. 基于 LengthFieldPrepender 第一个字段为长度字段，它可以计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
3. 基于 MessageToMessageDecoder<ByteBuf> 完成二次解码
4. 基于 MessageToMessageEncoder<ResponseMessage> 完成二次解码
5. client端基于 Future 进行结果响应式分发

## 协议数据结构

Frame(Message = MessageHeader + MessageBody(JSON)) = Length + version + opCode + streamId + operation/operation result