import React, { useState } from "react";
import LibraryButton from './component/LibraryButton'

function LibraryLogin() {
const [doYouHaveAnAccount, setDoYouHaveAnAccount] = useState(true);

//Login form
const [username, setUserName] = useState("");
const [password, setPassword] = useState("");

//Sign up form
const [studentId, setStudentId] = useState("");
const [email, setEmail] = useState("");
const [newPassword, setNewPassword] = useState("");
const [confirmPassword, setConfirmPassword] = useState("");

function navigateBetweenCreateAccountAndLogin() {
    setDoYouHaveAnAccount(!doYouHaveAnAccount);
}

function loginAction() {
    alert("This is your username " + username + " and password " + password);
}

function signUpAction() {
    if (newPassword !== confirmPassword) {
    alert("Passwords do not match");
    return;
    }
    alert("Account created for student " + studentId + " with email " + email);
}

return (
    <>
    {doYouHaveAnAccount ? (
        <div style={{ border: "3px solid blue", marginTop: "200px" }}>
          <label>Username:</label>{" "}
          <input
            type="text"
            value={username}
            onChange={(e) => setUserName(e.target.value)}
            placeholder="Enter your username"
        />
        <br />
        <label>Password:</label>{" "}
        <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
    />
        <br />
        <LibraryButton buttonLabel="LOGIN" onClickButton={loginAction} />
        <br />
        <label>New here? </label>
          <LibraryButton
            buttonLabel="Create an account"
            onClickButton={navigateBetweenCreateAccountAndLogin}
          />
        </div>
      ) : (
        <div style={{ border: "3px solid indigo", marginTop: "200px" }}>
          <label>Student ID:</label>{" "}
          <input
            type="text"
            value={studentId}
            onChange={(e) => setStudentId(e.target.value)}
            placeholder="Enter your student id"
        />
        <br />
        <label>Email:</label>{" "}
        <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <br />
          <label>Password:</label>{" "}
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <br />
          <label>Confirm Password:</label>{" "}
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          <br />
          <LibraryButton buttonLabel="Sign up" onClickButton={signUpAction} />
          <br />
          <label>Already have an account? </label>
          <LibraryButton
            buttonLabel="Go to Login"
            onClickButton={navigateBetweenCreateAccountAndLogin}
          />
        </div>
      )}
    </>
  );
}

export default LibraryLogin;