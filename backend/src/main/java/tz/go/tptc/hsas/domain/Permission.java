package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, length = 120)
    private String label;

    @Column(nullable = false, length = 50)
    private String module;

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
}
