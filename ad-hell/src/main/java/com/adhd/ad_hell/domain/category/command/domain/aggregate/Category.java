package com.adhd.ad_hell.domain.category.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE category SET status = 'DELETE' where id = ?")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "parent_id",
        foreignKey = @ForeignKey(name = "fk_category_parent")
    )
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    @Builder
    private Category(Category parent, String name, String description, CategoryStatus status) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void updateInfo(String name, String description) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(description)) {
            this.description = description;
        }
    }

    public void deleteRecursively() {
        this.status = CategoryStatus.DELETE;
        for (Category child : children) {
            child.deleteRecursively();
        }
    }
}
