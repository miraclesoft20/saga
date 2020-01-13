package ir.saga.mongo;


import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static ir.saga.mongo.ZonedDateTimeToDocumentConverter.DATE_TIME;
import static ir.saga.mongo.ZonedDateTimeToDocumentConverter.ZONE;

/*import org.springframework.lang.Nullable;*/

@ReadingConverter
public class DocumentToZonedDateTimeConverter implements Converter<Document, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(/*@Nullable*/ Document document) {
        if (document == null) return null;

        Date dateTime = document.getDate(DATE_TIME);
        String zoneId = document.getString(ZONE);
        ZoneId zone = ZoneId.of(zoneId);

        return ZonedDateTime.ofInstant(dateTime.toInstant(), zone);
    }
}
