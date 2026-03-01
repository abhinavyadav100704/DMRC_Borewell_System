package com.dmrc.borewell.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Integer stationId;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "line_id", nullable = false)
    private Integer lineId;

    @Column(name = "location")
    private String location;

    @Column(name = "platform_count")
    private Integer platformCount;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "station_type")
    private String stationType;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "maintenance_notes")
    private String maintenanceNotes;
}
