package ext.javaiscoffee.polaroad.test;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.test.SampleData;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSampleData is a Querydsl query type for SampleData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSampleData extends EntityPathBase<SampleData> {

    private static final long serialVersionUID = -285158932L;

    public static final QSampleData sampleData = new QSampleData("sampleData");

    public final StringPath detail = createString("detail");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QSampleData(String variable) {
        super(SampleData.class, forVariable(variable));
    }

    public QSampleData(Path<? extends SampleData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSampleData(PathMetadata metadata) {
        super(SampleData.class, metadata);
    }

}

