package ru.itaros.lsbrl.structure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

import ru.itaros.lsbrl.utils.EndianHelper;
import ru.itaros.lsbrl.utils.LSBLibException;
import ru.itaros.lsbrl.utils.UnresolveableInheritanceException;
import ru.itaros.lsbrl.utils.Unsigned;

public final class LSBNodeEntry extends LSBEntry {

	private ArrayList<LSBEntry> collection = new ArrayList<LSBEntry>();
	
	private long uint_offset; 
	
	public static LSBNodeEntry createFromOffset(RandomAccessFile reader, long offset, LSBIdDict iddict) throws IOException, UnresolveableInheritanceException, LSBLibException{
		reader.seek(offset);
		return new LSBNodeEntry(reader,offset,iddict);
	}
	
	protected LSBNodeEntry(RandomAccessFile reader, long offset, LSBIdDict iddict) throws IOException, UnresolveableInheritanceException, LSBLibException {
		super((int)Unsigned.asUnsigned32(EndianHelper.intFromReader(reader)));
		
		uint_offset = offset;
		//reader.seek(offset+4);
		
		//Evaluating expectations
		int attributes = (int)Unsigned.asUnsigned32(EndianHelper.intFromReader(reader));
		int nodes = (int)Unsigned.asUnsigned32(EndianHelper.intFromReader(reader));
		
		for(int i = 0 ; i < attributes; i++){
			collection.add(LSBEntry.createFromOffset(reader, reader.getFilePointer(), iddict,LSBEntryType.ATTRIBUTE));
		}
		
		//Loading Nodes
		for(int i = 0 ; i < nodes; i++){
			collection.add(LSBEntry.createFromOffset(reader, reader.getFilePointer(), iddict,LSBEntryType.NODE));
		}	
			
		
	}

	
	public Iterator<LSBEntry> getChildIterator(){
		return collection.iterator();
	}

	public void writeExpectations(RandomAccessFile writer) throws IOException{
		super.writeExpectations(writer);
		//Counting
		int attributes=0;
		int nodes=0;
		for(LSBEntry e:collection){
			if(e instanceof LSBAttributeEntry){
				attributes++;
			}
			if(e instanceof LSBNodeEntry){
				nodes++;
			}			
		}
		//Writing
		writer.write(EndianHelper.flipBytewise(attributes));
		writer.write(EndianHelper.flipBytewise(nodes));
	}
	
}
