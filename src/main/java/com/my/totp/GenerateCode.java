package com.my.totp;

import java.util.Scanner;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class GenerateCode {

	private DefaultCodeGenerator codeGenerator;
	private Thread thread;

	public GenerateCode(String secret) {
		try {
			TimeProvider timeProvider = new SystemTimeProvider();
			codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA512);
			DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
			verifier.setTimePeriod(30);
			verifier.setAllowedTimePeriodDiscrepancy(1);

			generateCode(secret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCode(String secret) {
		thread = new Thread(() -> {
			try {
				while (true) {
					long currentBucket = Math.floorDiv(new SystemTimeProvider().getTime(), 30);
					System.out.println(codeGenerator.generate(secret, currentBucket));
					Thread.sleep(30000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public static void main(String[] args) {
		String secret;
		if (args.length == 0) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter Secret:");
			secret = scanner.nextLine();
			scanner.close();
		} else
			secret = args[0];
		new GenerateCode(secret);
	}
}
