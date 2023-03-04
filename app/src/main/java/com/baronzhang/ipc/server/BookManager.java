package com.baronzhang.ipc.server;

import android.os.IInterface;
import android.os.RemoteException;

import com.baronzhang.ipc.Book;

import java.util.List;

/**
 *
 * 还记得我们前面介绍过的 IInterface 吗，它代表的就是服务端进程具体什么样的能力。
 * 因此我们需要定义一个 BookManager 接口，BookManager 继承自 IIterface，
 * 表明服务端具备什么样的能力。
 *
 * 这个类用来定义服务端 RemoteService 具备什么样的能力
 *
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com)
 */
public interface BookManager extends IInterface {

    List<Book> getBooks() throws RemoteException;

    void addBook(Book book) throws RemoteException;
}
