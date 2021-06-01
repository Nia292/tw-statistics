package com.thrallwars.statistics.util.rcon;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Log4j2
public class RConPacket {

    public final String message;
    public final int type;
    public final int id;

    public RConPacket(String message, int type, int id) {
        this.message = message;
        this.type = type;
        this.id = id;
    }

    public static RConPacket fromBytes(byte[] bytes) {
        if (bytes == null) {
            throw new InvalidRconPackageException("No package received");
        }
        if (bytes.length < 4) {
            throw new InvalidRconPackageException("No size field present in rcon package");
        }
        log.debug("Reading RCON package of size {}", bytes.length);
        // Read size of the RCON package
        var bufferSize = bytes.length;
        var packageSize = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        log.debug("Header specifies RCON package size of {}", packageSize);
        if (packageSize != (bufferSize - 4)) {
            throw new InvalidRconPackageException("Package size specified was " + packageSize + " but got package of actual size " + (bufferSize - 4));
        }
        if (bytes.length < 8) {
            throw new InvalidRconPackageException("No id field present in rcon package");
        }
        if (bytes.length < 12) {
            throw new InvalidRconPackageException("No type field present in rcon package");
        }
        // read type and field
        var id = ByteBuffer.wrap(bytes, 4, 4).getInt();
        var type = ByteBuffer.wrap(bytes, 8, 4).getInt();
        var bodyBytes = ByteBuffer.wrap(bytes, 12, packageSize - 12).array();
        var body = new String(bodyBytes, StandardCharsets.UTF_8).trim();
        return new RConPacket(body, type, id);
    }

    @SneakyThrows
    public byte[] toBytes() {
        byte[] body = (message + "\0").getBytes(StandardCharsets.UTF_8);
        int bodyLength = body.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write( ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(9 + bodyLength).array());
        byteArrayOutputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(id).array());
        byteArrayOutputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(type).array());
        byteArrayOutputStream.write(body);
        byteArrayOutputStream.write(ByteBuffer.allocate(1).put((byte) 0).array());
        return byteArrayOutputStream.toByteArray();
    }
}
