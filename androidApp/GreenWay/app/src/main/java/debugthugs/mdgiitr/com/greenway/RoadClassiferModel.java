package debugthugs.mdgiitr.com.greenway;

public class RoadClassiferModel {
    private String startLat, startLong, endLat, endLong, quality;

    public RoadClassiferModel(String startLat, String startLong, String endLat, String endLong, String quality) {
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.quality = quality;
    }

    public String getStartLat() {

        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLong() {
        return endLong;
    }

    public void setEndLong(String endLong) {
        this.endLong = endLong;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
