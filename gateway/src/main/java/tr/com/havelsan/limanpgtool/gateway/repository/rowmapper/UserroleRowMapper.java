package tr.com.havelsan.limanpgtool.gateway.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import tr.com.havelsan.limanpgtool.gateway.domain.Userrole;

/**
 * Converter between {@link Row} to {@link Userrole}, with proper type conversions.
 */
@Service
public class UserroleRowMapper implements BiFunction<Row, String, Userrole> {

    private final ColumnConverter converter;

    public UserroleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Userrole} stored in the database.
     */
    @Override
    public Userrole apply(Row row, String prefix) {
        Userrole entity = new Userrole();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
