package fr.isen.vmrs;


import android.widget.ImageView;
import android.widget.TextView;

//Methode pour remplir la listview
public class ViewHolder {
    private TextView nomView ;
    private TextView ipView ;
    private TextView infoView;
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
    public void setInfoView(TextView infoView) {this.infoView = infoView;}
    public TextView getNomView() {return nomView;}
    public TextView getIPView() {return ipView;}
    public TextView getEtatView() {return infoView;}
    public ImageView getImageView() {return imageView;}
    public void setNom(String nom) {
        this.nomView.setText(nom);
    }
    public void setIP (String ip) {
        this.ipView.setText(ip);
    }
    public void setEtat (String info) {
        this.infoView.setText(info);
    }
    public void setImage(int image) {this.imageView.setImageResource(image);}

}
