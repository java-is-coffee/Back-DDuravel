package javaiscoffee.polaroad.member;

import lombok.Data;

@Data
public class PasswordResetRequestDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
