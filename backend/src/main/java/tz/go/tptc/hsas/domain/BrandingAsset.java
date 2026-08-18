package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "branding_assets")
public class BrandingAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "asset_key", nullable = false, unique = true, length = 40)
    private String assetKey;

    @Column(name = "content_type", nullable = false, length = 80)
    private String contentType;

    @Column(name = "file_name", length = 160)
    private String fileName;

    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] data;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAssetKey() { return assetKey; }
    public void setAssetKey(String assetKey) { this.assetKey = assetKey; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
