package com.baronzhang.ipc.proxy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.baronzhang.ipc.Book;
import com.baronzhang.ipc.server.BookManager;
import com.baronzhang.ipc.server.Stub;

import java.util.List;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com)
 * 代理类自然需要实现 BookManager 接口
 *
 *
 */
public class Proxy implements BookManager {

    private static final String DESCRIPTOR = "com.baronzhang.ipc.server.BookManager";
    private static final String TAG = "Proxy";

    private IBinder remote;

    /**
     * Proxy 是在 Stub 的 asInterface 中创建
     *  Proxy 这一步就说明 Proxy 构造函数的入参是 BinderProxy，
     * @param remote   BinderProxy 对象
     */
    public Proxy(IBinder remote) {

        this.remote = remote;
    }

    public String getInterfaceDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public List<Book> getBooks() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        List<Book> result;

        Log.e(TAG, "getBooks: ");
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            remote.transact(Stub.TRANSAVTION_getBooks, data, replay, 0);
            replay.readException();
            result = replay.createTypedArrayList(Book.CREATOR);
        } finally {
            replay.recycle();
            data.recycle();
        }
        return result;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
//首先通过 Parcel 将数据序列化，然后调用 remote.transact()
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();

        Log.e(TAG, "addBook: ");
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (book != null) {
                data.writeInt(1);
                book.writeToParcel(data, 0);
            } else {
                data.writeInt(0);
            }
            remote.transact(Stub.TRANSAVTION_addBook, data, replay, 0);
            replay.readException();
        } finally {
            replay.recycle();
            data.recycle();
        }
    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
