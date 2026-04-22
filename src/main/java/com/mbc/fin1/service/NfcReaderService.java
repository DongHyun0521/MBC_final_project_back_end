package com.mbc.fin1.service;


import org.springframework.stereotype.Service;
// javax.smartcardio.* : 자바(JDK)에 기본 내장된 스마트카드(NFC/IC) 통신용 핵심 라이브러리 전체를 가져옴
import javax.smartcardio.*;
import java.util.List;

@Service
public class NfcReaderService {

    public String waitForCardAndReadUid() throws Exception {
        
        // PC 환경에 구성된 스마트카드 단말기 관리자(Factory) 객체를 불러옴
        TerminalFactory factory = TerminalFactory.getDefault();
        
        // 현재 컴퓨터 USB에 정상적으로 인식된 모든 리더기 목록을 가져옴
        List<CardTerminal> terminals = factory.terminals().list();

        // 만약 리더기 목록이 비어있다면 (USB가 안 꽂혀있거나 드라이버 문제 발생 시) 에러 발생시킴
        if (terminals.isEmpty()) {
            throw new Exception("연결된 리더기를 찾을 수 없습니다");
        }

        // ACR122 리더기만 찾기
        CardTerminal terminal = null;
        for (CardTerminal t : terminals) {
        	if (t.getName().toUpperCase().contains("122")) {
                terminal = t;
                break; 
            }
        }

        // 만약 못 찾았다면 에러 던지기
        if (terminal == null) {
            throw new Exception("ACR122U 단말기를 찾을 수 없습니다");
        }
        System.out.println("대기 중인 단말기: " + terminal.getName());

        // 리더기 위에 카드가 물리적으로 닿을 때까지 프로그램의 흐름을 멈추고 무한 대기
        terminal.waitForCardPresent(0);

        // 매개변수 "*" -> T=0, T=1 등 카드의 통신 프로토콜을 시스템이 알아서 자동으로 맞추도록 하는 것
        Card card = terminal.connect("*");
        
        // 카드와 APDU 명령어를 주고받을 수 있는 전용 데이터 통로(채널) 개방
        CardChannel channel = card.getBasicChannel();

        // 국제 표준 APDU 명령어 배열
        byte[] uidCommand = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
        
        // UID 요청 명령어(APDU) -> 카드 전송 -> 결과값 응답 객체에 저장
        ResponseAPDU response = channel.transmit(new CommandAPDU(uidCommand));

        card.disconnect(false);
        
        // getSW1(): 카드가 대답한 상태 코드의 첫 번째 값을 가져옴
        // 응답 상태 코드(SW1, SW2)가 90 00인지 확인 -> 스마트카드 표준에서 90 00은 정상 처리 의미
        if (response.getSW1() == 0x90 && response.getSW2() == 0x00) {
            
            // 카드에서 추출한 데이터 -> 바이트 배열 형태
            byte[] uidBytes = response.getData(); // getData(): 진짜 데이터(UID 고유번호)만 쏙 뽑아서 바이트 배열로 가져옴
            
            // 읽기 쉬운 문자열로 조립하기 위한 가변 버퍼 생성
            StringBuilder uidHex = new StringBuilder();
            
            for (byte b : uidBytes) {
                uidHex.append(String.format("%02X", b));
            }
            
            // 완성된 16진수 UID 문자열을 반환
            return uidHex.toString();
        } else {
            // 상태 코드가 90 00이 아니라면 읽기 에러이므로 예외 처리
            throw new Exception("카드 UID를 읽을 수 없습니다");
        }
    }
}