package com.scalar.ist.common;

public final class AssetIdFactory {

  private AssetIdFactory() {}

  public static String forCompany(String assetName, String companyId) {
    return String.format("%s-%s", assetName, companyId);
  }

  public static String forUserProfile(String assetName, String companyId, String holderId) {
    return String.format("%s-%s-%s", assetName, companyId, holderId);
  }

  public static String forMaster(String assetName, String organizationId, long createdAt) {
    return String.join("-", assetName, organizationId, String.valueOf(createdAt));
  }

  public static String forConsentStatement(
      String assetName, String version, String orgId, long createdAt) {
    return String.format("%s%s-%s-%s", assetName, version, orgId, String.valueOf(createdAt));
  }

  public static String forConsentStatus(
      String assetName, String statementId, String subjectId) {
    return String.join("-", assetName, statementId, subjectId);
  }

  public static String forThirdParty(String assetName, String companyId, String domain) {
    return String.format("%s-%s-%s", assetName, companyId, domain);
  }
}
