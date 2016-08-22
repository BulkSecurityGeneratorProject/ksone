package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;


/**
 * A CrossFitBox.
 */
@Entity
@Table(name = "CROSSFITBOX")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CrossFitBox implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull        
    @Column(name = "website_pattern", nullable = false)
    private String websitepattern;

    @NotNull        
    @Column(name = "adminwebsite", nullable = false)
    private String adminwebsite;

    @NotNull        
    @Column(name = "bookingwebsite", nullable = false)
    private String bookingwebsite;

    @NotNull        
    @Column(name = "rootwebsite", nullable = false)
    private String rootwebsite;

    @Email
    @NotNull        
    @Column(name = "email_from", nullable = false)
    private String emailFrom;
    
    @NotNull        
    @Column(name = "logo_url", nullable = true)
    private String logoUrl;
    
    @Column(name = "time_zone", nullable = true)
    private String timeZone;
    
    @Column(name = "nb_max_day_booking", nullable = true)
    private int nbMaxDayBooking = 7;

    @Column(name = "nb_hours_at_least_to_book", nullable = true)
    private int nbHoursAtLeastToBook = 24;
    
    @Column(name = "nb_hours_at_least_to_cancel", nullable = true)
    private int nbHoursAtLeastToCancel = 6;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsitepattern() {
        return websitepattern;
    }

    public void setWebsitepattern(String websitepattern) {
        this.websitepattern = websitepattern;
    }

    public String getAdminwebsite() {
        return adminwebsite;
    }

    public void setAdminwebsite(String adminwebsite) {
        this.adminwebsite = adminwebsite;
    }

    public String getBookingwebsite() {
        return bookingwebsite;
    }

    public void setBookingwebsite(String bookingwebsite) {
        this.bookingwebsite = bookingwebsite;
    }

    public String getRootwebsite() {
        return rootwebsite;
    }

    public void setRootwebsite(String rootwebsite) {
        this.rootwebsite = rootwebsite;
    }
    
   

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public int getNbMaxDayBooking() {
		return nbMaxDayBooking;
	}

	public void setNbMaxDayBooking(int nbMaxDayBooking) {
		this.nbMaxDayBooking = nbMaxDayBooking;
	}

	public int getNbHoursAtLeastToBook() {
		return nbHoursAtLeastToBook;
	}

	public void setNbHoursAtLeastToBook(int nbHoursAtLeastToBook) {
		this.nbHoursAtLeastToBook = nbHoursAtLeastToBook;
	}

	public int getNbHoursAtLeastToCancel() {
		return nbHoursAtLeastToCancel;
	}

	public void setNbHoursAtLeastToCancel(int nbHoursAtLeastToCancel) {
		this.nbHoursAtLeastToCancel = nbHoursAtLeastToCancel;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CrossFitBox crossFitBox = (CrossFitBox) o;

        if ( ! Objects.equals(id, crossFitBox.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CrossFitBox{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", websitepattern='" + websitepattern + "'" +
                ", adminwebsite='" + adminwebsite + "'" +
                ", bookingwebsite='" + bookingwebsite + "'" +
                ", rootwebsite='" + rootwebsite + "'" +
                '}';
    }
}