package fr.isen.vmrs;


import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    private TextView nomView ;
    private TextView ipView ;
    private TextView OSView;
    private ImageView imageView ;
    public void setNomView(TextView nomView) {
        this.nomView = nomView;
    }
    public void setIPView(TextView ipView) {
        this.ipView = ipView;
    }
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
    public void setOSView(TextView OSView) {this.OSView = OSView;}
    public TextView getNomView() {return nomView;}
    public TextView getIPView() {return ipView;}
    public TextView getOSView() {return OSView;}
    public ImageView getImageView() {return imageView;}
    public void setNom(String nom) {
        this.nomView.setText(nom);
    }
    public void setIP (String ip) {
        this.ipView.setText(ip);
    }
    public void setOS (String os) {
        this.OSView.setText(os);
    }
    public void setImage(int image) {this.imageView.setImageResource(image);}

}
