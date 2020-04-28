package cn.itrip.search.service;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.stereotype.Component;

@Component
public class Hotel {
    @Field("cityId")
    private Long cityId ;
    @Field("details")
    private String details;
    @Field("hotelName")
    private String hotelName;

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "cityId=" + cityId +
                ", keyword='" + details + '\'' +
                ", hotelName='" + hotelName + '\'' +
                '}';
    }
}
