/*
  *
  *  PROTOCOLO ZAMOBILE
  *  ======================
  *  Formato do protocolo
  *
  *
+----+-------+---------------+----------------+----------------+-------------------+-------------------+-------------------+
  *  |0x0F   |V PROT         | TAMANHO        | ID DISPOSITIVO |   ID COMANDO      | N DADOS           | CHECKSUM FLETCHER |
  *
+----+-------+---------------+----------------+----------------+-------------------+-------------------+-------------------+
  *  |       |               |                |                |                   |                   |
  *  |       |               |                |                |                   |                   |
  *  |       |               |                |                |                   |                   |
  *  |       |               |                |                |                   |                   +---------------------- CHECKSUM FLETCHER (O Checksum é a partir de VPROT até o fim de NDADOS)
  *  |       |               |                |                |                   |
  *  |       |               |                |                |                   +---------------------- N BYTES DADOS
  *  |       |               |                |                +--------------------------------------- 1 BYTE ID COMANDO
  *  |       |               |                |
  *  |       |               |                +----------------------------- 8 BYTES ID DISPOSITIVO
  *  |       |               |
  *  |       |               +---------------------------------------- 2 bytes com o TAMANHO (SOMA DE ID DISPOSITIVO (8 bytes) + ID COMANDO(1 byte) + N DADOS(n bytes) + CHECKSUM FLETCHER (2 bytes))
  *  |       |
  *  |       +----------------------------------------------------- 1 byte VERSÃO PROTOCOLO (INICIALMENTE 0)
  *  |
  *  |
  *  +------------------------------------------------------------ Início do quadro.
  *
  */

public class Zamobile {

     // Constantes do protocolo
     private static final byte INICIO_DO_QUADRO          = 0x0F;
     private static final byte VERSAO_DO_PROTOCOLO       = 0x00;
     private static final byte CMD_MSG_LIVRE             = 0x01;

     private static final int SIZEOF_HEADER              =  13;
     private static final int SIZEOF_FOOTER              =  2;

     // void
     public Zamobile() {}

     public byte[] createFrame(String message)
     {
         int index = 0;
         int size = (11 + message.length());
         byte[] frame = new byte[(size + 4)];

         frame[index++] = INICIO_DO_QUADRO;
         frame[index++] = VERSAO_DO_PROTOCOLO;

         /*
          *  TAMANHO
          *  2 bytes com o TAMANHO (SOMA DE ID DISPOSITIVO (8 bytes) + ID COMANDO(1 byte) + N DADOS(n bytes) + CHECKSUM FLETCHER (2 bytes))
          */
         frame[index++] = (byte)(size >>> 8);
         frame[index++] = (byte)size;

         /*
          *  ID DISPOSITIVO (8 bytes)
          */
         frame[index++] = (byte)'M';
         frame[index++] = (byte)'O';
         frame[index++] = (byte)'V';
         frame[index++] = (byte)'I';
         frame[index++] = (byte)'D';
         frame[index++] = (byte)'A';
         frame[index++] = (byte)'0';
         frame[index++] = (byte)'0';

         /*
          *  ID COMANDO (1 byte)
          */
         frame[index++] = CMD_MSG_LIVRE;

         /*
          *  N BYTES DADOS
          */
         for (int i = 0; i < message.length(); i++)
         {
             frame[index++] = (byte)message.charAt(i);
         }

         /*
          *  CHECKSUM FLETCHER
          *  O Checksum é a partir de VPROT até o fim de NDADOS
          */
         byte[] crc = fletcher(frame, frame.length);

         frame[index++] = crc[0];
         frame[index] = crc[1];

         return frame;
     }

     public String parseMessage(byte[] buffer, int size)
     {
         int i;
         int index = -1;

         for (i = 0; i < size; i++)
         {
             if (buffer[i]== INICIO_DO_QUADRO)
             {
                 index = i;
                 break;
             }
         }

         if (index < 0)
         {
             return null;
         }

         int length = 0;

         length = (byte)(buffer[index+2]);
         length = (length << 8);
         length = (byte)(buffer[index+3]);

         length = length + 4;

         if (length > size)
         {
             return null;
         }

         byte[] new_buffer = Arrays.copyOfRange(buffer, index, length);

         // Check CRC
         byte[] crc = fletcher(new_buffer, new_buffer.length);

         if ( (new_buffer[size - 2] != crc[0]) || (new_buffer[size - 1] != crc[1]) )
         {
             return "CRC invalid!";
         }

         String ret = "";

         for (i = SIZEOF_HEADER; i < (new_buffer.length-2); i++)
         {
             ret += String.format("%c", new_buffer[i]);
         }

         return ret;

     }

     private byte[] fletcher(byte[] buffer, int size)
     {
         byte[] checksum = new byte[2];

         checksum[0] = 0x00;
         checksum[1] = 0x00;

         for (int i = 1; i < (size-2); i++)
         {
             checksum[0] += buffer[i];
             checksum[1] += checksum[0];
         }

         return checksum;
     }

     public static int getMinimumFrameSize()
     {
         return (SIZEOF_HEADER+SIZEOF_FOOTER);
     }

}

