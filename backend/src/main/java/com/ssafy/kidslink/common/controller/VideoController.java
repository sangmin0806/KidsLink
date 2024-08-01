package com.ssafy.kidslink.common.controller;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
@Slf4j
public class VideoController {
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties);
        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }



    /**
     * Start recording a session
     * @param sessionId The Session ID
     * @return The Recording ID
     */
    @PostMapping("/sessions/{sessionId}/recordings/start")
    public ResponseEntity<String> startRecording(@PathVariable("sessionId") String sessionId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        System.out.println("녹화시작");
        RecordingProperties properties = new RecordingProperties.Builder()
                .outputMode(Recording.OutputMode.COMPOSED)
                .build();
        Recording recording = openvidu.startRecording(sessionId, properties);
        return new ResponseEntity<>(recording.getId(), HttpStatus.OK);
    }

    /**
     * Stop recording a session
     * @param recordingId The Recording ID
     * @return The stopped Recording
     */
    @PostMapping("/recordings/stop/{recordingId}")
    public ResponseEntity<Recording> stopRecording(@PathVariable("recordingId") String recordingId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        System.out.println("녹화중지");
        Recording recording = openvidu.stopRecording(recordingId);
        return new ResponseEntity<>(recording, HttpStatus.OK);
    }



    /**
     * Get a list of all recordings
     * @return The list of recordings
     */
    @GetMapping("/recordings")
    public ResponseEntity<List<Recording>> listRecordings() throws OpenViduJavaClientException, OpenViduHttpException {
        List<Recording> recordings = openvidu.listRecordings();
        return new ResponseEntity<>(recordings, HttpStatus.OK);
    }

    /**
     * Get a specific recording by ID
     * @param recordingId The Recording ID
     * @return The Recording
     */
    @GetMapping("/recordings/{recordingId}")
    public ResponseEntity<Recording> getRecording(@PathVariable("recordingId") String recordingId)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Recording recording = openvidu.getRecording(recordingId);
        return new ResponseEntity<>(recording, HttpStatus.OK);
    }


}
