package no.skytte.smileyface.model;

import android.content.ContentValues;

import no.skytte.smileyface.Utilities;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

//tilsynsobjektid;
// orgnummer;
// navn;
// adrlinje1;adrlinje2;
// postnr;
// poststed;
// tilsynid;
// sakref;
// status;
// dato;
// total_karakter;
// tilsynsbesoektype;
// tema1_no;tema1_nn;karakter1;tema2_no;tema2_nn;karakter2;tema3_no;tema3_nn;karakter3;tema4_no;tema4_nn;karakter4
public class InspectionDto {
    public String tilsynsobjektid;
    public String orgnummer;
    public String navn;
    public String adrlinje1;
    public String adrlinje2;
    public String postnr;
    public String poststed;
    public String tilsynid;
    public String sakref;
    public int status;
    public String dato;
    public int total_karakter;
    public int tilsynsbesoektype;
    public String tema1_no;
    public String tema1_nn;
    public int karakter1;
    public String tema2_no;
    public String tema2_nn;
    public int karakter2;
    public String tema3_no;
    public String tema3_nn;
    public int karakter3;
    public String tema4_no;
    public String tema4_nn;
    public int karakter4;

    public ContentValues getLocationValues() {
        ContentValues cv = new ContentValues();
        cv.put(LocationEntry.COLUMN_TO_ID, tilsynsobjektid);
        cv.put(LocationEntry.COLUMN_NAME, navn);
        cv.put(LocationEntry.COLUMN_ADDRESS, adrlinje1);
        cv.put(LocationEntry.COLUMN_POSTCODE, postnr);
        cv.put(LocationEntry.COLUMN_CITY, poststed);
        return cv;
    }

    public ContentValues getInspectionValues() {
        ContentValues cv = new ContentValues();
        cv.put(InspectionEntry.COLUMN_INSP_ID, tilsynid);
        cv.put(InspectionEntry.COLUMN_TO_ID, tilsynsobjektid);
        cv.put(InspectionEntry.COLUMN_DATE, Utilities.dateStringToMillis(dato));
        cv.put(InspectionEntry.COLUMN_STATUS, status);
        cv.put(InspectionEntry.COLUMN_TYPE, tilsynsbesoektype);
        cv.put(InspectionEntry.COLUMN_GRADE, total_karakter);
        cv.put(InspectionEntry.COLUMN_GRADE1, karakter1);
        cv.put(InspectionEntry.COLUMN_GRADE2, karakter2);
        cv.put(InspectionEntry.COLUMN_GRADE3, karakter3);
        cv.put(InspectionEntry.COLUMN_GRADE4, karakter4);
        return cv;
    }
}
