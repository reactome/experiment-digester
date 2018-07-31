package org.reactome.server.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import org.reactome.server.data.model.Experiment;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SerializationUtil {

    private static SerializationUtil instance;
    private static Kryo kryo;

    public static SerializationUtil get() {
        if(instance == null) {
            instance = new SerializationUtil();
        }
        return instance;
    }

    private SerializationUtil() {
        initialise();
    }

    private void initialise() {
        kryo = new Kryo();
        kryo.register(Experiment.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(TreeMap.class);
        kryo.register(URL.class);
        kryo.register(Arrays.asList( "" ).getClass(), new DefaultSerializers.ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new DefaultSerializers.CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new DefaultSerializers.CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList( "" ).getClass(), new DefaultSerializers.CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton( "" ).getClass(), new DefaultSerializers.CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap( "", "" ).getClass(), new DefaultSerializers.CollectionsSingletonMapSerializer());
    }

    public void storeExperiment(List<Experiment> experiments, File outputFile) throws FileNotFoundException {
        try(Output output = new Output(new FileOutputStream(outputFile))) {
            kryo.writeObject(output, experiments);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Experiment> loadExperiment(File inputFile) throws FileNotFoundException {
        try(Input input = new Input(new FileInputStream(inputFile))) {
            return kryo.readObject(input, ArrayList.class);
        }
    }
}
