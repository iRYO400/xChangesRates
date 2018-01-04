package workshop.akbolatss.greendaogenerator;


import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class DaoGenerator {

    public static void main(String[] arg) {
        Schema schema = new Schema(1, "workshop.akbolatss.xchangesrates.model"); // Your app package name and the (.db) is the folder where the DAO files will be generated into.
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new org.greenrobot.greendao.generator.DaoGenerator().generateAll(schema, "./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity data = addChartResponseData(schema);
        Entity info = addChartResponseDataInfo(schema);
        Entity charts = addChartResponseDataChart(schema);

        Property infoId = info.addLongProperty("infoId").notNull().getProperty();
        data.addToOne(info, infoId, "info");
    }


    private static Entity addChartResponseData(final Schema schema) {
        Entity data = schema.addEntity("ChartResponseData");
        data.addIdProperty().primaryKey().autoincrement();
        data.addStringProperty("exchange").notNull();
        data.addStringProperty("currency");
        data.addStringProperty("source");
        return data;
    }

    private static Entity addChartResponseDataInfo(final Schema schema) {
        Entity info = schema.addEntity("ChartResponseDataInfo");
        info.addIdProperty().primaryKey().autoincrement();
        info.addFloatProperty("volume");
        info.addStringProperty("high");
        info.addFloatProperty("change24");
        info.addStringProperty("last");
        info.addStringProperty("low");
        info.addStringProperty("buy");
        info.addStringProperty("sell");
        info.addFloatProperty("change");
        info.addStringProperty("started");
        info.addStringProperty("multiply");
        info.addStringProperty("updated");
        info.addLongProperty("timestamp");
        return info;
    }

    private static Entity addChartResponseDataChart(final Schema schema) {
        Entity chart = schema.addEntity("ReminderItem");
        chart.addIdProperty().primaryKey().autoincrement();
        chart.addFloatProperty("market");
        chart.addStringProperty("high");
        chart.addStringProperty("low");
        chart.addStringProperty("price");
        chart.addLongProperty("timestamp");
        return chart;
    }
}
