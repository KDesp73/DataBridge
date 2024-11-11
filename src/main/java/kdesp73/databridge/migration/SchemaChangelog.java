/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kdesp73.databridge.migration;

import java.sql.Timestamp;

/**
 *
 * @author kdesp73
 */
public class SchemaChangelog {
	private int versionNumber;
	private String migrationDescription;
	private String appliedAt;
	private String checksum;

	public int getVersionNumber() {
		return versionNumber;
	}

	public String getMigrationDescription() {
		return migrationDescription;
	}

	public String getAppliedAt() {
		return appliedAt;
	}

	public String getChecksum() {
		return checksum;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SchemaChangelog{");
		sb.append("versionNumber=").append(versionNumber);
		sb.append(", migrationDescription=").append(migrationDescription);
		sb.append(", appliedAt=").append(appliedAt);
		sb.append(", checksum=").append(checksum);
		sb.append('}');
		return sb.toString();
	}
	
	
}
