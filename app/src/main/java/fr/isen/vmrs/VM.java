package fr.isen.vmrs;

//classe VM contenant toutes les informations sur une VM
public class VM {
    private String ID;
    private String IP;
    private String Info;
    private String Name;
    private int Port;
    private String IdContainer;
    private String Apps[];
    private String AdDate;
    private String OS;
    private int Image;

    public VM (String ID, String IP, int Port, String Name){
        this.ID = ID;
        this.IP = IP;
        this.Port = Port;
        this.Name = Name;

        if (this.Name.indexOf("Debian") != -1) {
            Image=R.drawable.debian;
        } else if (this.Name.indexOf("Ubuntu") != -1) {
            Image=R.drawable.ubuntu;
        } else {
            Image=R.drawable.otheros;
        }
    }

    public String getID() {
        return ID;
    }
    public String getIP() {
        return IP;
    }
    public String getInfo() {
        return Info;
    }
    public String getName() {
        return Name;
    }
    public int getPort() {
        return Port;
    }
    public String getIdContainer() {
        return IdContainer;
    }
    public String[] getApps() {
        return Apps;
    }
    public int getNumberApps() {
        return Apps.length;
    }
    public String getAdDate() {
        return AdDate;
    }
    public String getOS() {
        return OS;
    }
    public int getImage() {
        return Image;
    }

    public void setInfo(String info) {
        this.Info = info;
    }
    public void setIdContainer(String idContainer) {
        this.IdContainer = idContainer;
    }
    public void setApps(String apps[]) {
        this.Apps = apps;
    }
    public void setAdDate(String adDate) {
        this.AdDate = adDate;
    }
    public void setOS(String OS) {this.OS = OS;}

}
