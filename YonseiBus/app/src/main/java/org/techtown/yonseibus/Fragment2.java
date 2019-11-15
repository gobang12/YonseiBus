package org.techtown.yonseibus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Fragment2 extends Fragment{

    LinearLayout linear01;
    LinearLayout linear02;
    LinearLayout busList01;
    LinearLayout busList02;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2,container,false);

        linear01 = rootView.findViewById(R.id.totalBus30);
        linear02 = rootView.findViewById(R.id.totalBus34);
        busList01 = rootView.findViewById(R.id.allBus1);
        busList02 = rootView.findViewById(R.id.allBus2);

        linear01.setBackgroundColor(0xff3366CC);

        ((MainActivity)getActivity()).setTimeList(rootView, this.getActivity());

        linear01.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                busList01.setVisibility(View.VISIBLE);
                busList02.setVisibility(View.GONE);
                linear01.setBackgroundColor(0xff3366CC);
                linear02.setBackgroundColor(Color.WHITE);

            }
        });

        linear02.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                busList01.setVisibility(View.GONE);
                busList02.setVisibility(View.VISIBLE);
                linear01.setBackgroundColor(Color.WHITE);
                linear02.setBackgroundColor(0xff3366CC);
            }
        });



        return rootView;
    }


}
