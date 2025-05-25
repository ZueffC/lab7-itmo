package itmo.lab5.shared;

import java.io.Serializable;

import itmo.lab5.shared.models.Flat;

public class DataPacket implements Serializable {
    private final CommandType type;
    private final Integer id;
    private final Flat flat;
    private String nick;
    private String password;

    public DataPacket(CommandType type, Integer id, Flat flat) {
        this.type = type;
        this.id = id;
        this.flat = flat;
    }

    public Flat getFlat() {
        return flat;
    }
    
    public CommandType getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return '\'' + "DataPacket{" +
                "type=" + type +
                ", message='" + id + '\'' +
                ", flat = " + flat + '}';
    }

    public String getNick() {
        return nick;
    }

    public DataPacket setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DataPacket setPassword(String password) {
        this.password = password;
        return this;
    }
}