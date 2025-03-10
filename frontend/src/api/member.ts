import noAuthAxios from "./token/noAuthAxios";
import useAppStore from "../stores/store";
import { getTeacherInfo, getParentInfo } from "./Info";
import { useTeacherInfoStore } from "../stores/useTeacherInfoStore";
import { useParentInfoStore } from "../stores/useParentInfoStore";
import {
  ParentSignupData,
  LoginData,
  TeacherSignupData,
} from "../types/member";

const API_BASE_URL = import.meta.env.VITE_API_KEY;

// 로그인 함수
export async function login(user: LoginData) {
  const setUserType = useAppStore.getState().setUserType;

  try {
    const response = await noAuthAxios.post("/user/login", user);

    if (response.data.status === "success") {
      const { role, token, expiredAt } = response.data.data;
      localStorage.setItem("accessToken", token);
      localStorage.setItem("expiredAt", expiredAt.toString());
      setUserType(role); // userType 저장

      if (role === "ROLE_TEACHER") {
        const teacherInfo = await getTeacherInfo();
        useTeacherInfoStore.getState().setTeacherInfo(teacherInfo);
      } else if (role === "ROLE_PARENT") {
        const parentInfo = await getParentInfo();
        useParentInfoStore.getState().setParentInfo(parentInfo);
      }

      return response.data.data;
    } else {
      throw new Error("Login failed: " + response.data.message);
    }
  } catch (error) {
    console.error("Error logging in:", error);
    throw error;
  }
}

// 학부모 회원가입 함수
export async function parentSignup(user: ParentSignupData) {
  const formData = new FormData();
  formData.append("username", user.username);
  formData.append("email", user.email || "");
  formData.append("password", user.password);
  formData.append("passwordConfirm", user.passwordConfirm);
  formData.append("name", user.name);
  formData.append("nickname", user.nickname || user.name);
  formData.append("tel", user.tel || "");

  formData.append("child.name", user.child.name);
  formData.append(
    "child.kindergartenClassId",
    user.child.kindergartenClassId.toString()
  );
  formData.append(
    "child.kindergartenName",
    user.child.kindergartenId.toString()
  );
  formData.append("child.gender", user.child.gender);
  formData.append("child.birth", user.child.birth);

  if (user.profile) {
    formData.append("profile", user.profile);
  }
  if (user.childProfile) {
    formData.append("childProfile", user.childProfile);
  }

  try {
    const response = await noAuthAxios.post(
      `${API_BASE_URL}/parent`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error signing up parent:", error);
    throw error;
  }
}

// 선생님 회원가입 함수
export async function teacherSignup(user: TeacherSignupData) {
  const formData = new FormData();
  formData.append("username", user.username);
  formData.append("email", user.email || "");
  formData.append("password", user.password);
  formData.append("passwordConfirm", user.passwordConfirm);
  formData.append("name", user.name);
  formData.append("nickname", user.nickname || user.name);
  formData.append("tel", user.tel || "");
  formData.append("kindergartenId", user.kindergartenId.toString());
  formData.append("kindergartenClassId", user.kindergartenClassId.toString());
  if (user.profile) {
    formData.append("profile", user.profile);
  }

  try {
    const response = await noAuthAxios.post(
      `${API_BASE_URL}/teacher`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error signing up teacher:", error);
    throw error;
  }
}

// 아이디 중복 검사 함수
export async function checkUsernameExists(username: string): Promise<boolean> {
  try {
    const response = await noAuthAxios.post("/user/exists", { username });

    if (response.data.status === "success") {
      return response.data.data;
    } else {
      throw new Error("Failed to check username: " + response.data.message);
    }
  } catch (error) {
    console.error("Error checking username:", error);
    throw error;
  }
}

// 로그아웃 함수
export async function logout() {
  try {
    const response = await noAuthAxios.post("user/logout");
    if (response.data.status === "success") {
      if (
        localStorage.getItem("accessToken") ||
        localStorage.getItem("expiredAt")
      ) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("expiredAt");
      }
      // zustand 상태 초기화
      useAppStore.getState().resetState();
      // 세션 스토리지에서 user-storage 키 제거
      sessionStorage.removeItem("user-storage");
      return response.data.data;
    } else {
      throw new Error("Logout failed: " + response.data.message);
    }
  } catch (error) {
    console.error("Error logging out:", error);
    throw error;
  }
}
