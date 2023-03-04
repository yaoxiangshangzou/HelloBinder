package com.baronzhang.ipc.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baronzhang.ipc.Book;
import com.baronzhang.ipc.R;
import com.baronzhang.ipc.server.BookManager;
import com.baronzhang.ipc.server.RemoteService;
import com.baronzhang.ipc.server.Stub;

import java.util.List;

/**
 * client
 */
public class ClientActivity extends AppCompatActivity {

    private BookManager bookManager;
    private boolean isConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnection) {
                    attemptToBindService();
                    return;
                }

                if (bookManager == null)
                    return;

                try {
                    Book book = new Book();
                    book.setPrice(101);
                    book.setName("编码  来自客户端");
//                    addBook()   如果 Client 和 Server 在同一个进程，那么直接就是调用这个方法。
//                    如果是远程调用，Client 想要调用 Server 的方法就需要通过 Binder 代理来完成，也就是上面的 Proxy。

//                    Client 进程通过系统调用陷入内核态，Client 进程中执行 addBook() 的线程挂起等待返回
                    bookManager.addBook(book);

                    Log.d("ClientActivity=====1", bookManager.getBooks().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void attemptToBindService() {

        Intent intent = new Intent(this, RemoteService.class);
        intent.setAction("com.baronzhang.ipc.server");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnection = true;
            //asInterface(IBinder binder) 拿到 BookManager 对象，这个 IBinder 类型的入参 binder 是驱动传给我们的
            bookManager = Stub.asInterface(service);
            if (bookManager != null) {
                try {
//                    调用了 Binder 本地对象的 addBook() 并将结果返回给驱动，
//                    驱动唤醒 Client 进程里刚刚挂起的线程并将结果返回
                    List<Book> books = bookManager.getBooks();
                    Log.d("ClientActivity=====2", books.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnection = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnection) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isConnection) {
            unbindService(serviceConnection);
        }
    }
}
