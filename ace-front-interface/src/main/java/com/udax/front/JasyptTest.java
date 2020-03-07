package com.udax.front;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JasyptTest {
    @Test
    public void testEncrypt() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");          // 加密的算法，这个算法是默认的
        config.setPassword("WA_19DAPWD!up^US");                        // 加密的密钥
        standardPBEStringEncryptor.setConfig(config);
        String plainText = "WAL!pw19FORap";
        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        System.out.println(encryptedText);
    }

    @Test
    public void testDe() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("WA_19DAPWD!up^US");
        standardPBEStringEncryptor.setConfig(config);
        String encryptedText = "FGRzjXBTJDeoywo4Wdj7yRJdHRR3WTyq";
        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);
        System.out.println(plainText);
    }

    public static void main(String[] args) {
        //        Optional<String> t = Optional.ofNullable(null).map();
//        System.out.println(t.get());z
        List<Integer> list1 = Arrays.asList(1,2,3,4,5);
        List<Integer> list2 = Arrays.asList(5,6,7);
        List<Integer> list = list1.stream().filter(one -> {
            List<Boolean> result = list2.stream().map(two -> one == two).collect(Collectors.toList());
            if (result.indexOf(true) > -1)
                return true;
            return false;
        }).collect(Collectors.toList());
        list.stream().forEach(System.out::println);
    }
}