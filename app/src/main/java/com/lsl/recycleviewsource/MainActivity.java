package com.lsl.recycleviewsource;

import android.os.Looper;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lsl.source.viewpage.PagerAdapter;
import com.lsl.source.viewpage.ViewPagerSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> mDatas;

    ViewPagerSource mViewPagerSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle);
        mViewPagerSource = findViewById(R.id.viewpager);


        mDatas = getData();
        Log.e("info--->", "init recycleview");
        //set layoutmanager
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true); //布局长宽 固定

        TestLayoutManager testLayoutManager = new TestLayoutManager(this);
        recyclerView.setLayoutManager(testLayoutManager);

        //set adapter
        recyclerView.setAdapter(new MyAdapter());
        //set divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        //test viewpager

        mViewPagerSource.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.destroyItem(container, position, object);
                container.removeView((ImageView) object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView imageView = new ImageView(container.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setImageResource(R.mipmap.ic_launcher_round);
                container.addView(imageView);
                return imageView;
            }
        });


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
                Toast toast = new Toast(MainActivity.this);
                toast.setView(button);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//                Toast.makeText(MainActivity.this, "test txt", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }

}
