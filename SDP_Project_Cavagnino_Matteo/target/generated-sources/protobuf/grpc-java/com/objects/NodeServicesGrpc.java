package com.objects;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: Objects.proto")
public final class NodeServicesGrpc {

  private NodeServicesGrpc() {}

  public static final String SERVICE_NAME = "com.objects.NodeServices";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.objects.Objects.Token,
      com.objects.Objects.Message> METHOD_SEND_TOKEN =
      io.grpc.MethodDescriptor.<com.objects.Objects.Token, com.objects.Objects.Message>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.objects.NodeServices", "sendToken"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.objects.Objects.Token.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.objects.Objects.Message.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServicesMethodDescriptorSupplier("sendToken"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.objects.Objects.Message,
      com.objects.Objects.Message> METHOD_SEND_MESSAGE =
      io.grpc.MethodDescriptor.<com.objects.Objects.Message, com.objects.Objects.Message>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.objects.NodeServices", "sendMessage"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.objects.Objects.Message.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.objects.Objects.Message.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServicesMethodDescriptorSupplier("sendMessage"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NodeServicesStub newStub(io.grpc.Channel channel) {
    return new NodeServicesStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NodeServicesBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NodeServicesBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NodeServicesFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NodeServicesFutureStub(channel);
  }

  /**
   */
  public static abstract class NodeServicesImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendToken(com.objects.Objects.Token request,
        io.grpc.stub.StreamObserver<com.objects.Objects.Message> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_TOKEN, responseObserver);
    }

    /**
     */
    public void sendMessage(com.objects.Objects.Message request,
        io.grpc.stub.StreamObserver<com.objects.Objects.Message> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_MESSAGE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_SEND_TOKEN,
            asyncUnaryCall(
              new MethodHandlers<
                com.objects.Objects.Token,
                com.objects.Objects.Message>(
                  this, METHODID_SEND_TOKEN)))
          .addMethod(
            METHOD_SEND_MESSAGE,
            asyncUnaryCall(
              new MethodHandlers<
                com.objects.Objects.Message,
                com.objects.Objects.Message>(
                  this, METHODID_SEND_MESSAGE)))
          .build();
    }
  }

  /**
   */
  public static final class NodeServicesStub extends io.grpc.stub.AbstractStub<NodeServicesStub> {
    private NodeServicesStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServicesStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServicesStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServicesStub(channel, callOptions);
    }

    /**
     */
    public void sendToken(com.objects.Objects.Token request,
        io.grpc.stub.StreamObserver<com.objects.Objects.Message> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_TOKEN, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendMessage(com.objects.Objects.Message request,
        io.grpc.stub.StreamObserver<com.objects.Objects.Message> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_MESSAGE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NodeServicesBlockingStub extends io.grpc.stub.AbstractStub<NodeServicesBlockingStub> {
    private NodeServicesBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServicesBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServicesBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServicesBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.objects.Objects.Message sendToken(com.objects.Objects.Token request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_TOKEN, getCallOptions(), request);
    }

    /**
     */
    public com.objects.Objects.Message sendMessage(com.objects.Objects.Message request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_MESSAGE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NodeServicesFutureStub extends io.grpc.stub.AbstractStub<NodeServicesFutureStub> {
    private NodeServicesFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServicesFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServicesFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServicesFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.objects.Objects.Message> sendToken(
        com.objects.Objects.Token request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_TOKEN, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.objects.Objects.Message> sendMessage(
        com.objects.Objects.Message request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_MESSAGE, getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_TOKEN = 0;
  private static final int METHODID_SEND_MESSAGE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NodeServicesImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NodeServicesImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_TOKEN:
          serviceImpl.sendToken((com.objects.Objects.Token) request,
              (io.grpc.stub.StreamObserver<com.objects.Objects.Message>) responseObserver);
          break;
        case METHODID_SEND_MESSAGE:
          serviceImpl.sendMessage((com.objects.Objects.Message) request,
              (io.grpc.stub.StreamObserver<com.objects.Objects.Message>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NodeServicesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NodeServicesBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.objects.Objects.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NodeServices");
    }
  }

  private static final class NodeServicesFileDescriptorSupplier
      extends NodeServicesBaseDescriptorSupplier {
    NodeServicesFileDescriptorSupplier() {}
  }

  private static final class NodeServicesMethodDescriptorSupplier
      extends NodeServicesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NodeServicesMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NodeServicesGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NodeServicesFileDescriptorSupplier())
              .addMethod(METHOD_SEND_TOKEN)
              .addMethod(METHOD_SEND_MESSAGE)
              .build();
        }
      }
    }
    return result;
  }
}
