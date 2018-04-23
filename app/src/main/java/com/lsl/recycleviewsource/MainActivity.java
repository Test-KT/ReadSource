package com.lsl.recycleviewsource;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle);
        mDatas = getData();
        Log.e("info--->", "init recycleview");
        //set layoutmanager
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true); //布局长宽 固定

        TestLayoutManager testLayoutManager=new TestLayoutManager(this);
        recyclerView.setLayoutManager(testLayoutManager);

        //set adapter
        recyclerView.setAdapter(new MyAdapter());
        //set divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            RecyclerView.ViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).textView.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return 30;
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
        }
    }


    static final String[] datas = {"Barry", "Bod", "Marry", "Joy", "Jack", "Russia", "Junia"};

    List<String> getData() {
        List<String> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            list.add(datas[random.nextInt(7)]);
        }

        return list;
    }


    public void showDialog(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Button button = new Button(MainActivity.this);
                button.setText("我是按钮");
                Looper.prepare();
                Toast toast=new Toast(MainActivity.this);
                toast.setView(button);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
//                Toast.makeText(MainActivity.this, "test txt", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }

}
