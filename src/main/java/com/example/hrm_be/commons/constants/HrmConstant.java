package com.example.hrm_be.commons.constants;

import java.util.Random;

public class HrmConstant {

  public static Random RANDOM = new Random();

  public static class ERROR {

    public static class AUTH {
      public static final String FAILED = "error.auth.failed";
      public static final String NOT_FOUND = "error.auth.credentials_not_found";
      public static final String NOT_ALLOWED = "error.auth.credentials_not_allowed";
      public static final String JWT_NOT_CREATED = "error.auth.credentials_jwt_not_created";
      public static final String CREDENTIALS_INVALID = "error.auth.credentials_invalid";
    }

    public static class REQUEST {
      public static final String INVALID_PATH_VARIABLE = "error.request.path_variable_invalid";
      public static final String INVALID_BODY = "error.request.body_invalid";
      public static final String INVALID_BODY_EMAIL = "error.request.body_invalid_email";
      public static final String INVALID_BODY_PASSWORD = "error.request.body_invalid_password";
      public static final String INVALID_PARAM = "error.request.param_invalid";
      public static final String INVALID_PARAM_OTP = "error.request.param_invalid_otp";
      public static final String INVALID_PARAM_EMAIL = "error.request.param_invalid_email";
      public static final String INVALID = "error.request.invalid";
    }

    public static class FILE {
      public static final String DELETE = "error.file.delete";
      public static final String CREATE = "error.file.create";
      public static final String UPDATE = "error.file.update";
      public static final String EXIST = "error.file.exist";
      public static final String NOT_EXIST = "error.file.not_exist";
      public static final String SAVE_TO_DB = "error.file.save_to_db";
      public static final String SAVE_TO_SOURCE = "error.file.save_to_source";
      public static final String DOWNLOAD = "error.file.download";
      public static final String ZIP = "error.file.zip";
      public static final String CORRUPTED = "error.file.corrupted";
      public static final String EMPTY = "error.file.empty";
      public static final String MOVE = "error.file.move";
    }

    public static class MINIO {
      public static final String INVALID_KEY = "error.minio.invalid_key";
      public static final String FILE_ALREADY_EXISTS = "error.minio.file_already_exists";
      public static final String RESPONSE = "error.minio.response";
      public static final String INSUFFICIENT_DATA = "error.minio.insufficient_data";
      public static final String INTERNAL_EXCEPTION = "error.minio.internal_exception";
      public static final String INVALID_RESPONSE = "error.minio.invalid_response";
      public static final String IO = "error.minio.io";
      public static final String NO_SUCH_ALGORITHM = "error.minio.no_such_algorithm";
      public static final String SERVER = "error.minio.server";
      public static final String XML_PARSER = "error.minio.xml_parser";
      public static final String EXTERNAL = "error.minio.external";
    }

    public static class CATEGORY {
      public static final String CREATE = "error.category.create";
      public static final String UPDATE = "error.category.update";
      public static final String DELETE = "error.category.delete";
      public static final String EXIST = "error.category.exist";
      public static final String NOT_CHILD = "error.category.not_child";
      public static final String NOT_EXIST = "error.category.not_exist";
    }

    public static class TYPE {
      public static final String CREATE = "error.type.create";
      public static final String UPDATE = "error.type.update";
      public static final String DELETE = "error.type.delete";
      public static final String EXIST = "error.type.exist";
      public static final String NOT_CHILD = "error.type.not_child";
      public static final String NOT_EXIST = "error.type.not_exist";
    }

    public static class PRODUCT {
      public static final String CREATE = "error.category.create";
      public static final String UPDATE = "error.category.update";
      public static final String DELETE = "error.category.delete";
      public static final String EXIST = "error.category.exist";
      public static final String NOT_CHILD = "error.category.not_child";
      public static final String NOT_EXIST = "error.category.not_exist";
    }

    public static class PLAYLIST {
      public static final String CREATE = "error.playlist.create";
      public static final String UPDATE = "error.playlist.update";
      public static final String DELETE = "error.playlist.delete";
      public static final String EXIST = "error.playlist.exist";
      public static final String NOT_EXIST = "error.playlist.not_exist";
    }

    public static class PLAYLIST_FILE_MAP {
      public static final String NULL_OR_EMPTY_IDS =
          "error.playlist_file_map.playlist_ids.file_ids_null_or_empty";
    }

    public static class LICENSE {
      public static final String CREATE = "error.license.create";
      public static final String UPDATE = "error.license.update";
      public static final String DELETE = "error.license.delete";
      public static final String EXIST = "error.license.exist";
      public static final String NOT_EXIST = "error.license.not_exist";
      public static final String UNMATCHED = "error.license.unmatched";
      public static final String EXPIRED = "error.license.expired";
      public static final String INVALID = "error.license.invalid";
      public static final String EXPAND_FAILED = "error.license.expand_failed";
      public static final String NOT_ASSIGNED_USER = "error.license.not_assigned_user";
      public static final String EXIST_BY_USER = "error.license.exist_by_user";
    }

    public static class DEVICE_GROUP {
      public static final String CREATE = "error.device_group.create";
      public static final String UPDATE = "error.device_group.update";
      public static final String DELETE = "error.device_group.delete";
      public static final String EXIST = "error.device_group.exist";
      public static final String NOT_EXIST = "error.device_group.not_exist";
    }

    public static class DEVICE {
      public static final String CREATE = "error.device.create";
      public static final String UPDATE = "error.device.update";
      public static final String DELETE = "error.device.delete";
      public static final String EXIST = "error.device.exist";
      public static final String NOT_EXIST = "error.device.not_exist";
      public static final String DOWNLOAD = "error.device.download";
    }

    public static class STORAGE_LOCATION {
      public static final String CREATE = "error.storage_location.create";
      public static final String UPDATE = "error.storage_location.update";
      public static final String DELETE = "error.storage_location.delete";
      public static final String EXIST = "error.storage_location.exist";
      public static final String NOT_CHILD = "error.storage_location.not_child";
      public static final String NOT_EXIST = "error.storage_location.not_exist";
    }

    public static class UNIT_CONVERSION {
      public static final String CREATE = "error.unit_conversion.create";
      public static final String UPDATE = "error.unit_conversion.update";
      public static final String DELETE = "error.unit_conversion.delete";
      public static final String EXIST = "error.unit_conversion.exist";
      public static final String NOT_CHILD = "error.unit_conversion.not_child";
      public static final String NOT_EXIST = "error.unit_conversion.not_exist";
    }

    public static class UNIT_OF_MEASUREMENT {
      public static final String CREATE = "error.unit_of_measurement.create";
      public static final String UPDATE = "error.unit_of_measurement.update";
      public static final String DELETE = "error.unit_of_measurement.delete";
      public static final String EXIST = "error.unit_of_measurement.exist";
      public static final String NOT_CHILD = "error.unit_of_measurement.not_child";
      public static final String NOT_EXIST = "error.unit_of_measurement.not_exist";
    }

    public static class USER {
      public static final String NOT_EXIST = "error.user.not_exist";
      public static final String EXIST = "error.user.exist";
      public static final String NOT_ASSIGNED_ROLE = "error.user.not_assigned_role";
      public static final String NOT_MATCH_CONFIRM_PASSWORD =
          "error.user.not_match_confirm_password";
    }

    public static class ROLE {
      public static final String NOT_EXIST = "error.role.not_exist";
      public static final String NOT_ALLOWED = "error.role.not_allowed";
      public static final String EXIST = "error.role.exist";
    }

    public static class OTP {
      public static final String INVALID = "error.otp.invalid";
      public static final String NOT_EXIST = "error.otp.not_exist";
      public static final String STATUS_INACTIVE = "error.otp.status_inactive";
      public static final String EXPIRED = "error.otp.expired";
      public static final String WAIT = "error.otp.wait";
      public static final String UPDATE = "error.otp.update";
      public static final String CREATE = "error.otp.create";
    }

    public static class SERVER {
      public static final String INTERNAL = "error.server.internal";
    }

    public static class BRANCH {
      public static final String CREATE = "error.branch.create";
      public static final String UPDATE = "error.branch.update";
      public static final String DELETE = "error.branch.delete";
      public static final String EXIST = "error.branch.exist";
      public static final String NOT_CHILD = "error.branch.not_child";
      public static final String NOT_EXIST = "error.branch.not_exist";
    }

    public static class BATCH {
      public static final String CREATE = "error.batch.create";
      public static final String UPDATE = "error.batch.update";
      public static final String DELETE = "error.batch.delete";
      public static final String EXIST = "error.batch.exist";
      public static final String NOT_CHILD = "error.batch.not_child";
      public static final String NOT_EXIST = "error.batch.not_exist";
    }

    public static class MANUFACTURER {
      public static final String CREATE = "error.manufacturer.create";
      public static final String UPDATE = "error.manufacturer.update";
      public static final String DELETE = "error.manufacturer.delete";
      public static final String EXIST = "error.manufacturer.exist";
      public static final String NOT_CHILD = "error.manufacturer.not_child";
      public static final String NOT_EXIST = "error.manufacturer.not_exist";
    }

    public static class INVENTORY_CHECK {
      public static final String CREATE = "error.inventory_check.create";
      public static final String UPDATE = "error.inventory_check.update";
      public static final String DELETE = "error.inventory_check.delete";
      public static final String EXIST = "error.inventory_check.exist";
      public static final String NOT_CHILD = "error.inventory_check.not_child";
      public static final String NOT_EXIST = "error.inventory_check.not_exist";
    }

    public static class INBOUND_BATCH_DETAIL {
      public static final String CREATE = "error.inventory_check.create";
      public static final String UPDATE = "error.inventory_check.update";
      public static final String DELETE = "error.inventory_check.delete";
      public static final String EXIST = "error.inventory_check.exist";
      public static final String NOT_CHILD = "error.inventory_check.not_child";
      public static final String NOT_EXIST = "error.inventory_check.not_exist";
    }

    public static class SPECIAL_CONDITION {
      public static final String CREATE = "error.special_condition.create";
      public static final String UPDATE = "error.special_condition.update";
      public static final String DELETE = "error.special_condition.delete";
      public static final String EXIST = "error.special_condition.exist";
      public static final String NOT_CHILD = "error.special_condition.not_child";
      public static final String NOT_EXIST = "error.special_condition.not_exist";
    }

    public static class PURCHASE {
      public static final String CREATE = "error.purchase.create";
      public static final String UPDATE = "error.purchase.update";
      public static final String DELETE = "error.purchase.delete";
      public static final String EXIST = "error.purchase.exist";
      public static final String NOT_CHILD = "error.purchase.not_child";
      public static final String NOT_EXIST = "error.purchase.not_exist";
    }

    public static class INVENTORY_CHECK_DETAILS {
      public static final String CREATE = "error.inventory_check_details.create";
      public static final String UPDATE = "error.inventory_check_details.update";
      public static final String DELETE = "error.inventory_check_details.delete";
      public static final String EXIST = "error.inventory_check_details.exist";
      public static final String NOT_CHILD = "error.inventory_check_details.not_child";
      public static final String NOT_EXIST = "error.inventory_check_details.not_exist";
    }

    public static class INBOUND_DETAILS {
      public static final String CREATE = "error.inbound_details.create";
      public static final String UPDATE = "error.inbound_details.update";
      public static final String DELETE = "error.inbound_details.delete";
      public static final String EXIST = "error.inbound_details.exist";
      public static final String NOT_CHILD = "error.inbound_details.not_child";
      public static final String NOT_EXIST = "error.inbound_details.not_exist";
    }

    public static class INBOUND {
      public static final String CREATE = "error.inbound.create";
      public static final String UPDATE = "error.inbound.update";
      public static final String DELETE = "error.inbound.delete";
      public static final String EXIST = "error.inbound.exist";
      public static final String NOT_CHILD = "error.inbound.not_child";
      public static final String NOT_EXIST = "error.inboud.not_exist";
    }

    public static class OUTBOUND_DETAILS {
      public static final String CREATE = "error.outbound_details.create";
      public static final String UPDATE = "error.outbound_details.update";
      public static final String DELETE = "error.outbound_details.delete";
      public static final String EXIST = "error.outbound_details.exist";
      public static final String NOT_CHILD = "error.outbound_details.not_child";
      public static final String NOT_EXIST = "error.outbound_details.not_exist";
    }

    public static class OUTBOUND {
      public static final String CREATE = "error.outbound.create";
      public static final String UPDATE = "error.outbound.update";
      public static final String DELETE = "error.outbound.delete";
      public static final String EXIST = "error.outbound.exist";
      public static final String NOT_CHILD = "error.outbound.not_child";
      public static final String NOT_EXIST = "error.outbound.not_exist";
    }

    public static class NOTIFICATION {
      public static final String CREATE = "error.notification.create";
      public static final String UPDATE = "error.notification.update";
      public static final String DELETE = "error.notification.delete";
      public static final String EXIST = "error.notification.exist";
      public static final String NOT_CHILD = "error.notification.not_child";
      public static final String NOT_EXIST = "error.notification.not_exist";
    }
  }
}
