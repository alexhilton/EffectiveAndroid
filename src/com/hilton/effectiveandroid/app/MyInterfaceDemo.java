package com.hilton.effectiveandroid.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

/*
 * <p>This is the same to the file generated by aidl tool, but looks much better.</p>
 * <p>1. Why aidl puts all classes into InterfaceDemo as inner classes?<br/>
 * Because we want to generate only one file, in which contains all necessary stuff.
 * For each file only one public class is allowed, so put others into the public class as
 * public inner classes in order to let both client side and server side to access them.<br/>
 * <p>2. Can we separate the client side stuff and server side stuff to make it more clear?</p>
 * <p>3. When client component and the server component are in the same package or, put it in
 * the other way, they run in the same process, no IPC actually happened. No Binder is involved.</p>
 */
public interface MyInterfaceDemo extends IInterface {
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends Binder implements MyInterfaceDemo {
        private static final String DESCRIPTOR = "com.hilton.effectiveandroid.app.InterfaceDemo";
        private static final String TAG = "InterfaceDemo.Stub";

        /** Construct the stub at attach it to the interface. */
        public Stub() {
            Log.e(TAG, "construction of Stub");
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an
         * com.hilton.effectiveandroid.app.InterfaceDemo interface, generating a
         * proxy if needed.
         */
        public static MyInterfaceDemo asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = (IInterface) obj.queryLocalInterface(DESCRIPTOR);
            /*
             * When the client side and the server side are in the same package(they run in
             * the same process) no real IPC happened, so you can get the server object
             * directly from here. Which means the obj should be ServiceStub.
             */
            if (((iin != null) && (iin instanceof MyInterfaceDemo))) {
                Log.e(TAG, "convert it to InterfaceDemo");
                return ((MyInterfaceDemo) iin);
            }
            return new Proxy(obj);
        }

        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            Thread.dumpStack();
            Log.e(TAG, "calling code " + code + ", " + data + ", " + reply);
            switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_sayHello: {
                data.enforceInterface(DESCRIPTOR);
                this.sayHello();
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_say: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                this.say(_arg0);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_getResponse: {
                data.enforceInterface(DESCRIPTOR);
                String _result = this.getResponse();
                reply.writeNoException();
                reply.writeString(_result);
                return true;
            }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements MyInterfaceDemo {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                Log.e(TAG, "proxy, create a Proxy object");
                mRemote = remote;
            }

            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            public void sayHello() throws RemoteException {
                Log.e(TAG, "sayHello");
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_sayHello, _data, _reply,
                            0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void say(String msg) throws RemoteException {
                Log.e(TAG, "say something");
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(msg);
                    mRemote.transact(Stub.TRANSACTION_say, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getResponse() throws RemoteException {
                Log.e(TAG, "getResponse");
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getResponse, _data,
                            _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_sayHello = (IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_say = (IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getResponse = (IBinder.FIRST_CALL_TRANSACTION + 2);
    }

    public void sayHello() throws RemoteException;

    public void say(String msg) throws RemoteException;

    public String getResponse() throws RemoteException;
}