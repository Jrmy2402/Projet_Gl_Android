package fr.isen.vmrs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//Classe permettant la mise en forme des VMs dans la listview
public class VMAdapter extends BaseAdapter {
    List<VM> vms;
    LayoutInflater inflater;

    public VMAdapter(Context context, List<VM> vms) {
        inflater = LayoutInflater.from(context);
        this.vms = vms;
    }

    @Override
    public int getCount() {
        return vms.size();
    }

    @Override
    public Object getItem(int position) {
        return vms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.vm_item,null);
            holder.setNomView((TextView)convertView.findViewById(R.id.nameVM));
            holder.setIPView((TextView)convertView.findViewById(R.id.ipVM));
            holder.setInfoView((TextView)convertView.findViewById(R.id.infoVM));
            holder.setImageView((ImageView)convertView.findViewById(R.id.avatar));
            convertView.setTag(holder);
        }
        else { holder = (ViewHolder) convertView.getTag(); }
        holder.setNom(vms.get(position).getName());
        if (vms.get(position).getIP() != "") {
            holder.setIP(vms.get(position).getIP() + ":" + vms.get(position).getPort());
        }else holder.setIP("");

        holder.setEtat(vms.get(position).getInfo());
        /*if (vms.get(position).getName().equalsIgnoreCase("Debian")) {
            holder.setImage(R.drawable.debian);
        } else if (vms.get(position).getName().equalsIgnoreCase("Ubuntu")) {
            holder.setImage(R.drawable.ubuntu);
        }*/
        //System.out.println("Adapter : " + vms.get(position).getImage());
        holder.setImage(vms.get(position).getImage());
        return convertView;
    }
}
