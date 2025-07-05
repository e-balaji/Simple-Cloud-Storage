package com.airtribe.scs.entity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    private String folderPath;

    @Enumerated(EnumType.STRING)
    private Visibility visibility= Visibility.PRIVATE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(nullable = false)
    private Integer version = 1;

    public String getFullPath() {
        return folderPath + "/" + filename;
    }
}
