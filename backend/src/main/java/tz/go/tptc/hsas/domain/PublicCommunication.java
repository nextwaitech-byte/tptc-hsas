package tz.go.tptc.hsas.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "public_communications")
public class PublicCommunication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "message_code", nullable = false, unique = true, length = 30)
    private String messageCode;

    @Column(name = "message_type", nullable = false, length = 40)
    private String messageType = "public_advisory";

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false, length = 40)
    private String channel = "sms";

    @Column(name = "target_region")
    private String targetRegion;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "recipients_count")
    private int recipientsCount;

    @Column(name = "acknowledgement_count")
    private int acknowledgementCount;

    @Column(nullable = false, length = 20)
    private String status = "draft";

    @Column(name = "sent_at")
    private Instant sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMessageCode() { return messageCode; }
    public void setMessageCode(String messageCode) { this.messageCode = messageCode; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getTargetRegion() { return targetRegion; }
    public void setTargetRegion(String targetRegion) { this.targetRegion = targetRegion; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public int getRecipientsCount() { return recipientsCount; }
    public void setRecipientsCount(int recipientsCount) { this.recipientsCount = recipientsCount; }
    public int getAcknowledgementCount() { return acknowledgementCount; }
    public void setAcknowledgementCount(int acknowledgementCount) { this.acknowledgementCount = acknowledgementCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
