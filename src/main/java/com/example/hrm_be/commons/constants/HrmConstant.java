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

    public static class STORAGE_LOCATION {
      public static final String CREATE = "error.type.create";
      public static final String UPDATE = "error.type.update";
      public static final String DELETE = "error.type.delete";
      public static final String EXIST = "error.type.exist";
      public static final String NOT_CHILD = "error.type.not_child";
      public static final String NOT_EXIST = "error.type.not_exist";
    }
    public static class UNIT_CONVERSION {
      public static final String CREATE = "error.type.create";
      public static final String UPDATE = "error.type.update";
      public static final String DELETE = "error.type.delete";
      public static final String EXIST = "error.type.exist";
      public static final String NOT_CHILD = "error.type.not_child";
      public static final String NOT_EXIST = "error.type.not_exist";
    }
    public static class UNIT_OF_MEASUREMENT {
      public static final String CREATE = "error.type.create";
      public static final String UPDATE = "error.type.update";
      public static final String DELETE = "error.type.delete";
      public static final String EXIST = "error.type.exist";
      public static final String NOT_CHILD = "error.type.not_child";
      public static final String NOT_EXIST = "error.type.not_exist";
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

    public static class INGREDIENT {
      public static final String CREATE = "error.ingredient.create";
      public static final String UPDATE = "error.ingredient.update";
      public static final String DELETE = "error.ingredient.delete";
      public static final String EXIST = "error.ingredient.exist";
      public static final String NOT_CHILD = "error.ingredient.not_child";
      public static final String NOT_EXIST = "error.ingredient.not_exist";
    }

    public static class MANUFACTURER {
      public static final String CREATE = "error.branch.create";
      public static final String UPDATE = "error.branch.update";
      public static final String DELETE = "error.branch.delete";
      public static final String EXIST = "error.branch.exist";
      public static final String NOT_CHILD = "error.branch.not_child";
      public static final String NOT_EXIST = "error.branch.not_exist";
    }
  }
}
