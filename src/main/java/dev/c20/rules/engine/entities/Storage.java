package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name= "C20_STG")
public class Storage implements Serializable {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="C20_STG")
        private Long id;

        @Column(name="C20_IS_FOLDER" , columnDefinition = "TINYINT")
        private Boolean isFolder = null;

        @Column(name="C20_NAME" )
        private String name;

        @Column(name="C20_EXTENSION" )
        private String extension;

        @Column(name="C20_IMAGE")
        private String image;

        @Column(name="C20_CREATED")
        private Date created;

        @Column(name="C20_CREATOR")
        private String creator;

        @Column(name="C20_DATE_ASSIGNED")
        private Date dateAssigned;

        @Column(name="C20_ASSIGNED")
        private String assigned;


        @Column(name="C20_DELETED", columnDefinition = "TINYINT" )
        private Boolean deleted = null;

        @Column(name="C20_DELETE_DATE")
        @Temporal(value= TemporalType.TIMESTAMP)
        private Date deletedDate;

        @Column(name="C20_DELETER")
        private String userDeleter;

        @Column(name="C20_MODIFIED", columnDefinition = "TINYINT" )
        private Boolean modified = null;

        @Column(name="C20_MODIFY_DATE")
        @Temporal(value= TemporalType.TIMESTAMP)
        private Date modifyDate;

        @Column(name="C20_MODIFIER")
        private String modifier;

        @Column(name="C20_FILE_ID")
        private Long fileId;

        @Column(name="C20_READONLY", columnDefinition = "TINYINT" )
        private Boolean readOnly = false;

        @Column(name="C20_VISIBLE", columnDefinition = "TINYINT" )
        private Boolean visible = true;

        @Column(name="C20_LOCKED", columnDefinition = "TINYINT" )
        private Boolean locked = false;

        @Column(name="C20_RESTRICTED_BY_ROLE", columnDefinition = "TINYINT" )
        private Boolean restrictedByPerm = false;

        @Column(name="C20_RESTRICTED_CHILDREN_BY_ROLE", columnDefinition = "TINYINT" )
        private Boolean childrenRestrictedByPerm = false;

        @Column(name="C20_STATUS")
        private Integer status;

        @Column(name="C20_CLAZZ_NAME")
        private String clazzName;

        @Column(name="C20_LEVEL")
        private Integer level = 0;

        @Column(name="C20_DESCRIPTION", length = 200)
        private String description;

        @Column(name="C20_PATH",length = 1000 )
        private String path;

}
