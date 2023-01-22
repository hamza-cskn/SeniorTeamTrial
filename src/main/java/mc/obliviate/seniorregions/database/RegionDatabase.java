package mc.obliviate.seniorregions.database;

import com.google.common.base.Preconditions;
import mc.obliviate.seniorregions.Cuboid;
import mc.obliviate.seniorregions.Region;
import mc.obliviate.seniorregions.util.ListSerializer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionDatabase implements IRegionDatabase {

    private Connection connection;

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testDB", "root", "12345678");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void save(Region region) {
        if (exist("SELECT * FROM regions")) {
            System.out.println(new ListSerializer<UUID>().serializeList(region.getPlayers()));
            System.out.println(region.getPlayers());
            update("UPDATE regions SET region = '" + region.getName() +
                    "', cuboid = '" + region.getCuboid().serialize() +
                    "', whitelist = '" + new ListSerializer<UUID>().serializeList(region.getPlayers()) + "'");
        } else {
            update("INSERT INTO regions (region, cuboid, whitelist) VALUES ('"
                    + region.getName() +
                    "', '" + region.getCuboid().serialize() + "', '"
                    + new ListSerializer<UUID>().serializeList(region.getPlayers()) + "')");
        }
    }

    @Override
    public Region load(Object id) {
        try {
            ResultSet rs = query(String.format("SELECT * FROM regions WHERE region='{0}'", id));
            Preconditions.checkState(rs.next(), "result set is empty.");
            return deserializeRegion(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rs open result set
     * @return deserialized region
     */
    private Region deserializeRegion(ResultSet rs) throws SQLException {
        final String name = rs.getString("region");
        final String serializedCuboid = rs.getString("cuboid");
        final String serializedWhitelist = rs.getString("whitelist");
        final List<UUID> whitelist = new ListSerializer<UUID>().deserializeList(serializedWhitelist, UUID::fromString);
        final Cuboid cuboid = Cuboid.deserialize(serializedCuboid);
        return new Region(cuboid, name, whitelist);
    }

    @Override
    public List<Region> loadAll() {
        final List<Region> result = new ArrayList<>();
        final ResultSet rs = query("SELECT * FROM regions");
        try {
            while (rs.next()) {
                result.add(deserializeRegion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void update(String sql) {
        try {
            System.out.println(sql);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResultSet query(String sql) {
        try {
            System.out.println(sql);
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exist(String sql) {
        ResultSet rs = query(sql);
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
