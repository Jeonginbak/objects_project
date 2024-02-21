package com.objects.marketbridge.review.domain;

import com.objects.marketbridge.member.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSurvey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_survey_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private Long reviewSurveyCategoryId;

    private String surveyCategory;

    private String content;

    @Builder
    public ReviewSurvey(Long id, Review review, Long reviewSurveyCategoryId, String surveyCategory, String content) {
        this.id = id;
        this.review = review;
        this.reviewSurveyCategoryId = reviewSurveyCategoryId;
        this.surveyCategory = surveyCategory;
        this.content = content;
    }






    public void update(String content) {
        this.content = content;
    }

}
