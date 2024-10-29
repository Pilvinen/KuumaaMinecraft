package starandserpent.minecraft.criticalfixes;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;

@NTable(name = "unique_visitor", schema = "", catalog = "")
public class UniqueVisitor extends NEntity<String> {

    @JsonProperty("visitor_uuid") String visitorUUID;

    // Required for NDatabase. Do not remove.
    public UniqueVisitor() {}

    public UniqueVisitor(String key, String uuid) {
        this.setKey(key);
        this.visitorUUID = uuid;
    }

}
