package com.handheld.IDCardDemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdhe.idcarddemo.R;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018-09-26.
 */

public class GridViewAdapter extends BaseAdapter{

    private Context mContext;
    private List<Map<String,Object>> mList;
    private LayoutInflater mInflater;

    public GridViewAdapter(Context context, List<Map<String,Object>> list){
        this.mContext=context;
        this.mList=list;
        this.mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gridview_item,null);

            holder.tv_grid_item = convertView.findViewById(R.id.text);
            holder.iv_grid_item = convertView.findViewById(R.id.img);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> map = mList.get(position);

        holder.tv_grid_item.setText(String.valueOf(map.get("text")));
        holder.iv_grid_item.setImageResource((Integer) map.get("image"));

        switch (position){
            case 0:
                convertView.setBackgroundColor(mContext.getColor(R.color.new_msg_info));
                break;
            case 1:
                convertView.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
                break;
            case 2:
                convertView.setBackgroundColor(mContext.getColor(R.color.light_zi));
                break;
            case 3:
                convertView.setBackgroundColor(mContext.getColor(R.color.topChenJin));
                break;
            case 4:
                convertView.setBackgroundColor(mContext.getColor(R.color.zise));
                break;
            case 5:
                convertView.setBackgroundColor(mContext.getColor(R.color.green));
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position){
                    case 0://高亭
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/gtjp/"));
                        break;
                    case 1://秀山
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/xsjp/"));
                        break;
                    case 2://长涂
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/ctjp/"));
                        break;
                    case 3://衢山
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/qsjp/"));
                        break;
                    case 4://小洋山
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/xysjp/"));
                        break;
                    case 5://嵊泗
                        mContext.startActivity(new Intent(mContext,MatouActivity.class).putExtra("url","http://221.12.158.166:9099/ssjp/"));
                        break;
                }
            }
        });

        return convertView;
    }

    public class ViewHolder{
        public TextView tv_grid_item;
        public ImageView iv_grid_item;
    }

}
