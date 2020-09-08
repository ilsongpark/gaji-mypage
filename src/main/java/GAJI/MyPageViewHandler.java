package GAJI;

import GAJI.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRegistered_then_CREATE_1 (@Payload Registered registered) {
        try {
            if (registered.isMe()) {
                // view 객체 생성
                MyPage myPage = new MyPage();
                // view 객체에 이벤트의 Value 를 set 함
                myPage.setProductId(registered.getId());
                myPage.setStatus(registered.getStatus());
                myPage.setSellerId(registered.getMemberId());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenBought_then_UPDATE_1(@Payload Bought bought) {
        try {
            if (bought.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByProductId(bought.getProductId());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setStatus(bought.getStatus());
                    myPage.setBuyerId(bought.getMemberId());
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCanceled_then_UPDATE_2(@Payload Canceled canceled) {
        try {
            if (canceled.isMe()) {
                // view 객체 조회
                List<MyPage> myPageList = myPageRepository.findByProductId(canceled.getProductId());
                for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setStatus(canceled.getStatus());
                    myPage.setBuyerId(null);
                    // view 레파지 토리에 save
                    myPageRepository.save(myPage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void whenDeleted_then_DELETE_1(@Payload Deleted deleted) {
        try {
            if (deleted.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                System.out.println("Deleted..................");
                System.out.println(deleted.getId());
                myPageRepository.deleteByProductId(deleted.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}